package org.guce.web.process.atm.services;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.AvisTech;
import org.guce.process.atm.repositories.AvisTechHistoryRepository;
import org.guce.process.atm.repositories.impl.AvisTechRepositoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class AvisTechServiceTest {
    protected static List<AvisTech> list;

    @Mock
    protected AvisTechRepositoryImpl avisTechRepository;

    @Mock
    protected AvisTechHistoryRepository historyRepository;

    @Mock
    protected IdentifiantGeneratorLocal generator;

    @Mock
    protected CoreChargerFacadeLocal chargerFacade;

    @Mock
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @InjectMocks
    protected AvisTechService instance;

    public AvisTechServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            AvisTech c = new AvisTech();
            c.setId(1 + "");
            c.setActive(true);
            c.setRegisterNumber(nextString(17));
            c.setImmatriculation(nextString(17));
            c.setTechnicalName(nextString(17));
            c.setDeliveryDate(nextString(17));
            c.setExpiryDate(GuceCalendarUtil.getCalendar().getTime());
            c.setStatus(nextString(17));
            list.add(c);
        }
    }

    @Before
    public void setUp() {
        Mockito.when(avisTechRepository.count()).thenReturn(list.size());
        Mockito.when(avisTechRepository.findAll()).thenReturn(list);
        Mockito.when(avisTechRepository.find(ArgumentMatchers.any(String.class))).thenReturn(list.get(0));
        Mockito.doAnswer(new Answer<Void>() {
                    @Override
                    public Void answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        AvisTech c = (AvisTech)args[0];
                        c.setId("1");
                        return null;
                    }
                }).when(avisTechRepository).create(ArgumentMatchers.any(AvisTech.class));Mockito.when(avisTechRepository.search(ArgumentMatchers.any(SearchFilter.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(list);
        Mockito.when(avisTechRepository.searchCount(ArgumentMatchers.any(SearchFilter.class))).thenReturn(list.size());
    }

    @Test
    public void testCount() {
        System.out.println("count");
        int expResult = list.size();
        int result = instance.count();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testFindAll_0args() {
        System.out.println("findAll");
        List<AvisTech> expResult = list;
        List<AvisTech> result = instance.findAll();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testFindBy() {
        System.out.println("findBy");
        String primaryKey = "1";
        AvisTech expResult = list.get(0);
        AvisTech result = instance.findBy(primaryKey);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSave_One() {
        System.out.println("save");
        AvisTech entity = list.get(0);
        entity.setId(null);
        AvisTech expResult = entity;
        AvisTech result = instance.save(entity);
        Mockito.verify(avisTechRepository, Mockito.times(1)).create(entity);
        Assert.assertEquals(expResult, result);
        result = instance.save(entity);
        Mockito.verify(avisTechRepository, Mockito.times(1)).edit(entity);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testRemove_One() {
        System.out.println("remove");
        AvisTech entity = list.get(0);
        instance.remove(entity);
        Mockito.verify(avisTechRepository, Mockito.times(1)).edit(entity);
    }

    @Test
    public void testRemove_List() {
        System.out.println("remove");
        instance.remove(list);
        Mockito.verify(avisTechRepository, Mockito.times(10)).edit(ArgumentMatchers.any(AvisTech.class));
    }

    @Test
    public void testSave_List() {
        System.out.println("save");
        instance.save(list);
        Mockito.verify(avisTechRepository, Mockito.times(10)).edit(ArgumentMatchers.any(AvisTech.class));
    }

    @Test
    public void testSearch() {
        System.out.println("search");
        SearchFilter filter = new SearchFilter();
        int start = 0;
        int count = 0;
        String orderField = "";
        String order = "";
        List<AvisTech> expResult = list;
        List<AvisTech> result = instance.search(filter, start, count, orderField, order);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSearchCount() {
        System.out.println("searchCount");
        SearchFilter filter = new SearchFilter();
        int expResult = list.size();
        int result = instance.searchCount(filter);
        Assert.assertEquals(expResult, result);
    }

    public static String nextString(int size) {
        byte[] array = new byte[size];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
